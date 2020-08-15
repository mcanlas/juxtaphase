package controllers

import javax.inject._

import play.api.mvc._

@Singleton
class CompileController @Inject() (protected val controllerComponents: ControllerComponents) extends BaseController {
  def compile: Action[String] =
    Action(parse.text) { implicit req: Request[String] =>
      Ok(req.body)
    }
}
