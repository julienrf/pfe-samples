define(['ui', 'routes'], (Ui, routes) ->
  (node, id) ->
    ui = Ui(node)
    ui.forEachClick(() ->
      xhr = new XMLHttpRequest()
      route = routes.controllers.Items.delete(id)
      xhr.open(route.method, route.url)
      xhr.addEventListener('readystatechange', () ->
        if xhr.readyState == XMLHttpRequest.DONE
          if xhr.status == 200
            ui.delete()
          else
            alert('Unable to delete the item!')
      )
      xhr.send()
    )
)