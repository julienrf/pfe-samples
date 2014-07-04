package controllers;

public class Controller extends play.mvc.Controller {

    protected final Service service;

    public Controller(Service service) {
        this.service = service;
    }

}
