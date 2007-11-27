
function lenyaGetTitle(num) {
  return document.getElementById('lenyaTabTitle' + num);
}

function lenyaGetBody(num) {
  return document.getElementById('lenyaTabBody' + num);
}

function lenyaInitTabs(count) {
  lenyaToggleTab(count, 0);
}

function lenyaToggleTab(count, num) {
  for (var i = 0; i < count; i++) {
    var title = document.getElementById('lenyaTabTitle' + i);
    var body = document.getElementById('lenyaTabBody' + i);
    if (i == num) {
      title.className = 'lenyaTabTitleActive';
      body.className = 'lenyaTabBodyActive';
    }
    else {
      title.className = 'lenyaTabTitle';
      body.className = 'lenyaTabBody';
    }
  }
}