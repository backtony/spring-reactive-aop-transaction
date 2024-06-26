package com.example.aopwithtransaction.aop

import com.example.aopwithtransaction.service.aop.InterceptService
import com.example.aopwithtransaction.utils.proceedCoroutine
import com.example.aopwithtransaction.utils.runCoroutine
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import kotlin.coroutines.coroutineContext

@Aspect
//@Order(1)
@Component
class LoggingAspect(
    private val interceptService: InterceptService,
) {

    private val log = KotlinLogging.logger {}


    @Around("@annotation(com.example.aopwithtransaction.aop.Logging)")
    fun logging(joinPoint: ProceedingJoinPoint): Any? {
        return mono {
            log.info { "Aop Logging started" }

            val result = joinPoint.proceed().let { result ->
                if (result is Mono<*>) {
                    result.awaitSingleOrNull()
                } else {
                    result
                }
            }

            log.info { "Aop Logging completed" }

            result
        }
    }

    @Around("@annotation(com.example.aopwithtransaction.aop.CoroutineLogging)")
    fun coroutineLogging(joinPoint: ProceedingJoinPoint): Any? {
        return joinPoint.runCoroutine {
            val context = coroutineContext
            log.info { "coroutine aop context : $context" }
            interceptService.intercept()

            val result = joinPoint.proceedCoroutine().let { result ->
                if (result is Mono<*>) {
                    result.awaitSingleOrNull()
                } else {
                    result
                }
            }

            interceptService.intercept()
            result
        }
    }
}
