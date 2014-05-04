(function () {
  var handleDeleteClick = function (btn) {
    btn.addEventListener('click', function (e) {
      var xhr = new XMLHttpRequest();
      var route = routes.controllers.Items.delete(btn.dataset.id);
      xhr.open(route.method, route.url);
      xhr.addEventListener('readystatechange', function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
          if (xhr.status === 200) {
            var li = btn.parentNode;
            li.parentNode.removeChild(li);
          } else {
            alert('Unable to delete the item!');
          }
        }
      });
      xhr.send();
    });
  };

  var deleteBtns = document.querySelectorAll('button.delete-item');
  for (var i = 0, l = deleteBtns.length ; i < l ; i++) {
    handleDeleteClick(deleteBtns[i]);
  }
})();