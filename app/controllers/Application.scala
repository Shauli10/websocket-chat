package controllers

import models._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {
  
  /**
   * Just display the home page.
   */
  def index = Action { implicit request =>
    Ok(views.html.index())
  }
  
  /**
   * Display the chat room page.
   */
  def chatRoom(username: Option[String]) = Action { implicit request =>
    username.filterNot(_.isEmpty).map { username =>
      Ok(views.html.chatRoom(username))
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Please choose a valid username."
      )
    }
  }
  
  /**
   * Handles the chat websocket.
   */
  def chat(username: String) = WebSocket.tryAccept[JsValue] {
    request =>
      try {
        // ChatRoom.join returns (iteratee,enumerator)
        ChatRoom.join(username).map(e => Right(e))
      } catch {
        case e: Exception =>
          Future { Left(Ok("Failed")) } // Error here
      }
  }
}
