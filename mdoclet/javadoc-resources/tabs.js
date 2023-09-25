function openTab(evt) {
  let group = evt.currentTarget.parentNode.dataset.tabGroup;
  let name = evt.currentTarget.dataset.tabName;
  
  let tabLinks = document.querySelectorAll
    ('.tab-nav[data-tab-group="' + group + '"] button');
  for (let link of tabLinks) {
    if (link.dataset.tabName == name) {
      link.className="selected";
    } else {
      link.className="";
    }
  }
    
  var tabContents= document.querySelectorAll
    ('.tab-content[data-tab-group="' + group + '"]');
  for (let content of tabContents) {
    if (content.dataset.tabName == name) {
      content.style.display="block";
    } else {
      content.style.display="none";
    }
  }
}
