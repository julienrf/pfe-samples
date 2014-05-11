define(() ->
  (node) ->
    delete: () ->
      li = node.parentNode
      li.parentNode.removeChild(li)
    forEachClick: (callback) ->
      node.addEventListener('click', callback)
)