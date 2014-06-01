require(['logic'], (Logic) ->
  for deleteBtn in document.querySelectorAll('button.delete-item')
    do (deleteBtn) -> Logic(deleteBtn, deleteBtn.dataset.id)
)