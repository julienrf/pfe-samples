require(['routes'], (routes) ->

  class BidUi
    constructor: (item, ctl) ->
      @root = document.createElement('div')
      @bids = document.createElement('ul')
      @root.appendChild(@bids)
      name = document.createElement('input')
      name.type = 'text'
      name.placeholder = 'Your name'
      name.required = 'required'
      bid = document.createElement('input')
      bid.type = 'number'
      bid.placeholder = 'Your offer'
      bid.min = item.price
      bid.required = 'required'
      btn = document.createElement('button')
      btn.textContent = 'Bid!'
      @root.appendChild(name)
      @root.appendChild(bid)
      @root.appendChild(btn)
      btn.addEventListener('click', () ->
        ctl.bidClicked(name.value, bid.value)
      )
    updateBids: (bids) ->
      ul = document.createElement('ul')
      for name, price of bids
        li = document.createElement('li')
        li.textContent = "#{name}: #{price} €"
        ul.appendChild(li)
      @root.replaceChild(ul, @bids)
      @bids = ul

  class BidCtl
    constructor: (@item) ->
      @bids = {}
      @ui = new BidUi(@item, this)
      notifications = new EventSource(routes.controllers.Auctions.notifications(@item.id).url)
      notifications.addEventListener('message', (e) =>
        bid = JSON.parse(e.data)
        @addBid(bid.name, bid.price)
      )
    bidClicked: (name, price) ->
      if name != '' && price > @item.price
        xhr = new XMLHttpRequest()
        route = routes.controllers.Auctions.bid(@item.id)
        xhr.open(route.method, route.url)
        xhr.setRequestHeader('Content-Type', 'application/json')
        xhr.send(JSON.stringify({ name: name, price: +price }))
      else
        alert('Please enter your name and bid!')
    addBid: (name, price) ->
      @bids[name] = price
      @ui.updateBids(@bids)

  el = document.getElementById('auction-room')
  bid = new BidCtl(JSON.parse(el.dataset.state))
  el.appendChild(bid.ui.root)
)