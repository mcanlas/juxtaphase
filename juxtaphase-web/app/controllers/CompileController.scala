package controllers

import javax.inject._

import play.api._
import play.api.mvc._

@Singleton
class CompileController @Inject()(protected val controllerComponents: ControllerComponents) extends BaseController {
  def compile: Action[AnyContent] = Action { implicit req: Request[AnyContent] =>
    Ok("hello world")
  }
}