(() ->
  handleDeleteClick = (btn) ->
    btn.addEventListener('click', (e) ->
      xhr = new XMLHttpRequest()
      route = routes.controllers.Items.delete(btn.dataset.id)
      xhr.open(route.method, route.url)
      xhr.addEventListener('readystatechange', () ->
        if xhr.readyState == XMLHttpRequest.DONE
          if xhr.status == 200
            li = btn.parentNode
            li.parentNode.removeChild(li)
          else
            alert('Unable to delete the item!')
      )
      xhr.send()
    )

  for deleteBtn in document.querySelectorAll('button.delete-item')
    do (deleteBtn) -> handleDeleteClick(deleteBtn)
)()