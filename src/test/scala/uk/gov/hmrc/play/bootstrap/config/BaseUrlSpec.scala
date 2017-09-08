/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.play.bootstrap.config

import com.typesafe.config.ConfigException
import org.scalatest.{MustMatchers, WordSpec}
import play.api.Configuration

class BaseUrlSpec extends WordSpec with MustMatchers {

  trait TestSpec extends BaseUrl

  "BaseUrl" must {

    val service = "my-service"
    val prefix  = s"microservice.services.$service"

    "return a path when all configuration is provided" in new TestSpec {

      override val configuration: Configuration = Configuration(
        s"$prefix.protocol" -> "http",
        s"$prefix.host"     -> "localhost",
        s"$prefix.port"     -> "80"
      )

      val result: String = baseUrl(service)
      result mustEqual "http://localhost:80"
    }

    "fallback to default protocol when it is ommitted" in new TestSpec {

      override val configuration: Configuration = Configuration(
        "microservice.services.protocol"  -> "http",
        s"$prefix.host"                   -> "localhost",
        s"$prefix.port"                   -> "80"
      )

      val result: String = baseUrl(service)
      result mustEqual "http://localhost:80"
    }

    "fallback to `http` when default is ommitted" in new TestSpec {

      override val configuration: Configuration = Configuration(
        s"$prefix.host"                   -> "localhost",
        s"$prefix.port"                   -> "80"
      )

      val result: String = baseUrl(service)
      result mustEqual "http://localhost:80"
    }

    "throw a configuration exception when no service block exists" in new TestSpec {

      override val configuration: Configuration = Configuration.empty

      val e: ConfigException.Missing = intercept[ConfigException.Missing] {
        baseUrl(service)
      }

      e.getMessage() mustEqual "No configuration setting found for key 'microservice'"
    }

    "throw a configuration exception when no `host` exists" in new TestSpec {

      override val configuration: Configuration = Configuration(
        s"$prefix.port" -> "80"
      )

      val e: ConfigException.Missing = intercept[ConfigException.Missing] {
        baseUrl(service)
      }

      e.getMessage() mustEqual "No configuration setting found for key 'host'"
    }

    "throw a configuration exception when no `port` exists" in new TestSpec {

      override val configuration: Configuration = Configuration(
        s"$prefix.host" -> "localhost"
      )

      val e: ConfigException.Missing = intercept[ConfigException.Missing] {
        baseUrl(service)
      }

      e.getMessage() mustEqual "No configuration setting found for key 'port'"
    }
  }
}
