/*
 * =========================================================================================
 * This file is derivative work of
 * https://github.com/kamon-io/Kamon/blob/master/kamon-scala/src/main/scala/kamon/scala/instrumentation/FutureInstrumentation.scala
 * originally from the Kamon project (http://kamon.io)
 * and MODIFIED by the committers of Scalog.
 *
 * Original copyright notice follows.
 * =========================================================================================
 * Copyright © 2013-2014 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */

package io.rbricks.scalog.contextpropagation

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation._

import org.slf4j.MDC

trait Container {
  var stored: java.util.Map[String, String]
}

private[contextpropagation] class C(var stored: java.util.Map[String, String]) extends Container

@Aspect
class FutureInstrumentation {

  @DeclareMixin("scala.concurrent.impl.CallbackRunnable || scala.concurrent.impl.Future.PromiseCompletingRunnable")
  def mixinContainerToFutureRelatedRunnable: Container = new C(null)

  @Pointcut("execution((scala.concurrent.impl.CallbackRunnable || scala.concurrent.impl.Future.PromiseCompletingRunnable).new(..)) && this(runnable)")
  def futureRelatedRunnableCreation(runnable: Container): Unit = {}

  @After("futureRelatedRunnableCreation(runnable)")
  def afterCreation(runnable: Container): Unit = {
    runnable.stored = MDC.getCopyOfContextMap()
  }

  @Pointcut("execution(* (scala.concurrent.impl.CallbackRunnable || scala.concurrent.impl.Future.PromiseCompletingRunnable).run()) && this(runnable)")
  def futureRelatedRunnableExecution(runnable: Container) = {}

  @Around("futureRelatedRunnableExecution(runnable)")
  def aroundExecution(pjp: ProceedingJoinPoint, runnable: Container): Any = {
    val prev = MDC.getCopyOfContextMap()
    if (runnable.stored != null)
      MDC.setContextMap(runnable.stored)
    else
      MDC.clear()
    pjp.proceed()
    if (prev != null)
      MDC.setContextMap(prev)
    else
      MDC.clear()
  }

}

