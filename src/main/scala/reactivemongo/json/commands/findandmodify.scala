/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package reactivemongo.json.commands

import play.api.libs.json.{ Json, JsObject, JsUndefined, OWrites }, Json.JsValueWrapper

import reactivemongo.api.commands.{
FindAndModifyCommand,
ResolvedCollectionCommand
}
import reactivemongo.json.JSONSerializationPack

object JSONFindAndModifyCommand extends FindAndModifyCommand[JSONSerializationPack.type] {
  val pack: JSONSerializationPack.type = JSONSerializationPack
}

object JSONFindAndModifyImplicits {
  import JSONFindAndModifyCommand._
  import reactivemongo.utils.option

  implicit object FindAndModifyResultReader extends DealingWithGenericCommandErrorsReader[FindAndModifyResult] {
    def readResult(result: JsObject): FindAndModifyResult =
      FindAndModifyResult(
        (result \ "lastErrorObject").asOpt[JsObject].map { doc =>
          UpdateLastError(
            updatedExisting = (doc \ "updatedExisting").
              asOpt[Boolean].getOrElse(false),
            n = (doc \ "n").asOpt[Int].getOrElse(0),
            err = (doc \ "err").asOpt[String],
            upsertedId = {
              (doc \ "upserted") match {
                case _: JsUndefined => None
                case js             => Some(js)
              }
            })
        },
        (result \ "value").asOpt[JsObject])
  }

  implicit object FindAndModifyWriter
    extends OWrites[ResolvedCollectionCommand[FindAndModify]] {

    def writes(command: ResolvedCollectionCommand[FindAndModify]): JsObject = {
      val optionalFields = List[Option[(String, JsValueWrapper)]](
        command.command.sort.map("sort" -> _),
        command.command.fields.map("fields" -> _)).flatten

      Json.obj(
        "findAndModify" -> command.collection,
        "query" -> command.command.query) ++
        Json.obj(optionalFields: _*) ++
        (command.command.modify match {
          case Update(document, fetchNewObject, upsert) => Json.obj(
            "update" -> document,
            "new" -> fetchNewObject,
            "upsert" -> upsert)

          case Remove => Json.obj("remove" -> true)
        })
    }
  }
}
