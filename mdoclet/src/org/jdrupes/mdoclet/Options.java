/*
 * JDrupes MDoclet
 * Copyright 2013 Raffael Herzog
 * Copyright (C) 2017 Michael N. Lipp
 * 
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package org.jdrupes.mdoclet;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdrupes.mdoclet.processors.FlexmarkProcessor;

import com.sun.javadoc.DocErrorReporter;
import com.sun.tools.doclets.standard.Standard;

/**
 * Processes and stores the command line options.
 */
public class Options {

	public static final String OPT_MARKDOWN_PROCESSOR = "-markdown-processor";
    public static final String OPT_DISABLE_HIGHLIGHT = "-disable-highlight";
    public static final String OPT_DISABLE_AUTO_HIGHLIGHT = "-disable-auto-highlight";
    public static final String OPT_HIGHLIGHT_STYLE = "-highlight-style";
    public static final String OPT_ENCODING = "-encoding";
    public static final String OPT_OVERVIEW = "-overview";
    public static final String OPT_OUTPUT_DIR = "-d";
    public static final String OPT_STYLESHEETFILE = "-stylesheetfile";
    public static final String OPT_TAG = "-tag";

    private static final Pattern OPT_TAG_PATTERN 
    	= Pattern.compile("(?<tag>.*?)(?<!\\\\):(?<flags>[^:]*)(:(?<title>.*))?");
    
    private File overviewFile = null;
    private Charset encoding = null;
    private File destinationDir = null;
    private File stylesheetFile = null;
    private boolean highlightEnabled = true;
    private String highlightStyle = null;
    private Set<String> markedDownTags = new HashSet<>();
    
    private static MarkdownProcessor processor = new FlexmarkProcessor();
    private static boolean processorUsed = false;
    private static List<String[]> processorOptions = new ArrayList<>();

    public static int optionLength(String option) {
    	
    	if (option.startsWith("-M") || option.startsWith("-T")) {
    		return 1;
    	}
		switch (option) {
		case OPT_MARKDOWN_PROCESSOR:
		case OPT_HIGHLIGHT_STYLE:
		case OPT_DISABLE_HIGHLIGHT:
			return 2;
		case OPT_DISABLE_AUTO_HIGHLIGHT:
			return processor.isSupportedOption
					(MarkdownProcessor.INTERNAL_OPT_DISABLE_AUTO_HIGHLIGHT) + 1;
		default:
			return Standard.optionLength(option);
		}
    }

    /**
     * As specified by the Doclet specification.
     *
     * @param options          The command line options.
     * @param errorReporter    An error reporter to print messages.
     *
     * @return `true` if the options are valid.
     *
     * @see com.sun.javadoc.Doclet#validOptions(String[][], com.sun.javadoc.DocErrorReporter)
     */
    public static boolean validOptions(String[][] options, DocErrorReporter errorReporter) {
        String[][] forwardedOptions = new Options().load(options, errorReporter);
        if ( forwardedOptions != null ) {
            return Standard.validOptions(options, errorReporter);
        }
        else {
            return false;
        }
    }

    /**
     * Loads the options from the command line.
     *
     * @param options          The command line options.
     * @param errorReporter    The error reporter for printing messages.
     *
     * @return The options to be forwarded to the standard doclet.
     */
    public String[][] load(String[][] options, DocErrorReporter errorReporter) {
        LinkedList<String[]> optionsList = new LinkedList<>();
        for (String[] opt: options) {
        	optionsList.add(Arrays.copyOf(opt, opt.length));
        }
        Iterator<String[]> optionsIter = optionsList.iterator();
        while ( optionsIter.hasNext() ) {
            if ( !handleOption(optionsIter, errorReporter) ) {
                return null;
            }
        }
        return optionsList.toArray(new String[optionsList.size()][]);
    }

    protected boolean handleOption(Iterator<String[]> optionsIter, DocErrorReporter errorReporter) {
        String[] opt = optionsIter.next();
        switch (opt[0]) {
        
        // Standard options that we need to know about (i.e. copy)
        case OPT_ENCODING:
            try {
                encoding = Charset.forName(opt[1]);
            }
            catch ( IllegalCharsetNameException e ) {
                errorReporter.printError("Illegal charset: " + opt[1]);
                return false;
            }
            return true;

        case OPT_OUTPUT_DIR:
            if ( destinationDir != null ) {
                errorReporter.printError(OPT_OUTPUT_DIR + " may only be specified once");
            }
            setDestinationDir(new File(opt[1]));
            return true;

        case OPT_STYLESHEETFILE:
        	if ( stylesheetFile != null ) {
        		errorReporter.printError(OPT_STYLESHEETFILE + " may only specified once");
            }
        	setStylesheetFile(new File(opt[1]));
        	return true;

        case OPT_TAG:
        	Matcher matcher = OPT_TAG_PATTERN.matcher(opt[1]);
        	if (!matcher.matches()) {
        		return true;
        	}
        	if (!matcher.group("flags").contains("M")) {
        		return true;
        	}
        	markedDownTags.add(matcher.group("tag").replace("\\", ""));
        	opt[1] = matcher.group("tag") 
        			+ ":" + matcher.group("flags").replace("M", "");
        	if (matcher.group("title") != null) {
        			opt[1] += ":" + matcher.group("title");
        	}
        	return true;
        	
        // Standard options that we consume (i.e. don't forward)
        case OPT_OVERVIEW:
            if ( getOverviewFile() != null ) {
                errorReporter.printError(OPT_OVERVIEW + " may only be specified once");
                return false;
            }
            setOverviewFile(new File(opt[1]));
            // Don't remove the option. It must be passed to the standard doclet
            // if you want the overview to be generated even if there is only one
            // package to be documented. As we set the root doc comment, the file
            // will eventually not be processed by the standard doclet.
            return true;
            
        // Our own options
        case OPT_MARKDOWN_PROCESSOR:
        	if (processorUsed) {
        		errorReporter.printError("Markdown processor cannot be changed"
        				+ " after setting its options");
        		return false;
        	}
        	MarkdownProcessor p = createProcessor(opt, errorReporter);
        	if (p == null) {
        		return false;
        	}
        	processor = p;
        	return true;
            
        case OPT_DISABLE_HIGHLIGHT:
        	highlightEnabled = false;
          	optionsIter.remove();
          	return true;
          	
        case OPT_DISABLE_AUTO_HIGHLIGHT:
        	if (processor.isSupportedOption
        			(MarkdownProcessor.INTERNAL_OPT_DISABLE_AUTO_HIGHLIGHT) == -1) {
        		return false;
        	}
        	processorOptions.add(new String[] 
        			{ MarkdownProcessor.INTERNAL_OPT_DISABLE_AUTO_HIGHLIGHT });
        	optionsIter.remove();
        	return true;
        	
        case OPT_HIGHLIGHT_STYLE:
        	if ( highlightStyle != null ) {
        		errorReporter.printError("Only one " + OPT_HIGHLIGHT_STYLE 
        				+ " option allowed");
        		return false;
        	}
        	highlightStyle = opt[1];
        	optionsIter.remove();

        default:
        	break;
        }

        if (opt[0].startsWith("-M")) {
        	// Processor (type) may not be changed after providing options for it
        	processorUsed = true;
        	String[] pOpt = unpackOption(opt[0].substring(2));
        	if (processor.isSupportedOption(pOpt[0]) == -1) {
        		return false;
        	}
        	processorOptions.add(pOpt);
        	optionsIter.remove();
        	return true;
        }
        
        return true;
    }

    private static String[] unpackOption(String option) {
    	List<String> opts = new ArrayList<>();
    	String[] eSplit = option.split("=", 2);
    	opts.add(eSplit[0]);
    	if (eSplit.length > 1) {
    		String[] cSplit = eSplit[1].split("(?<!\\\\),");
    		Arrays.stream(cSplit).map(s -> s.replace("\\,", ","))
    				.forEach(s -> opts.add(s));
    	}
    	return opts.toArray(new String[opts.size()]);
    }
    
	private MarkdownProcessor createProcessor(String[] opt, DocErrorReporter errorReporter) {
		try {
			@SuppressWarnings("unchecked")
			Class<MarkdownProcessor> mpc = (Class<MarkdownProcessor>) 
					getClass().getClassLoader().loadClass(opt[1]);
			return (MarkdownProcessor)mpc.newInstance(); 
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | ClassCastException e) {
			errorReporter.printError("Markdown processor \"" + opt[1] 
					+ "\" cannot be loaded (" + e.getMessage()
					+ "), check name and docletpath");
			return null;
		}
	}

    /**
     * Returns the processor selected by the options.
     * 
     * @return the processor
     */
    public MarkdownProcessor getProcessor() {
    	return processor;
    }

    /**
     * Returns the markdown processor options filtered out in
     * {@link #load(String[][], DocErrorReporter)}.
     * 
     * @return the options
     */
    public String[][] getProcessorOptions() {
    	return processorOptions.toArray(new String[processorOptions.size()][]);
    }

    /**
     * Returns the names of the tags that have been seen in the `-tag` options
     * with an "`M`" flag.
     * 
     * @return the tags
     */
    public String[] getMarkedDownTags() {
    	return markedDownTags.toArray(new String[markedDownTags.size()]);
    }
    
    /**
     * Gets the overview file.
     *
     * @return The overview file.
     */
    public File getOverviewFile() {
        return overviewFile;
    }

    /**
     * Sets the overview file.
     *
     * @param overviewFile The overview file.
     */
    public void setOverviewFile(File overviewFile) {
        this.overviewFile = overviewFile;
    }

    /**
     * Gets the source encoding.
     *
     * @return The source encoding.
     */
    public Charset getEncoding() {
        return encoding != null ? encoding : Charset.defaultCharset();
    }

    /**
     * Sets the source encoding.
     *
     * @param encoding The source encoding.
     */
    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    /**
     * Gets the destination directory.
     *
     * @return The destination directory.
     */
    public File getDestinationDir() {
        if ( destinationDir == null ) {
            destinationDir = new File(System.getProperty("user.dir"));
        }
        return destinationDir;
    }

    /**
     * Sets the destination directory.
     *
     * @param destinationDir    The destination directory
     */
    public void setDestinationDir(File destinationDir) {
        this.destinationDir = destinationDir;
    }

    /**
     * Gets the CSS stylesheet file.
     *
     * @return The stylesheet file.
     */
    public File getStylesheetFile() {
        return stylesheetFile;
    }

    /**
     * Sets the CSS stylesheet file.
     *
     * @param stylesheetFile The stylesheet file.
     */
    public void setStylesheetFile(File stylesheetFile) {
        this.stylesheetFile = stylesheetFile;
    }

    public boolean isHighlightEnabled() {
        return highlightEnabled;
    }

    public void setHighlightEnabled(boolean highlightEnabled) {
        this.highlightEnabled = highlightEnabled;
    }

    public String getHighlightStyle() {
        return highlightStyle != null ? highlightStyle : "default";
    }

    public void setHighlightStyle(String highlightStyle) {
        this.highlightStyle = highlightStyle;
    }

}
