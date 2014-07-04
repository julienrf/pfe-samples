package controllers

class Controller(val service: Service) extends play.api.mvc.Controller {

  implicit val app = service.app

}
