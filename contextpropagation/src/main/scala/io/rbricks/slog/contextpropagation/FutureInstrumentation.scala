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

