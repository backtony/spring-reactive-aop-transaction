package com.example.aopwithtransaction.service.aop

import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.stereotype.Service
import kotlin.coroutines.coroutineContext

@Service
class InterceptService() {
    private val log = KotlinLogging.logger { }

    suspend fun intercept() {
        delay(100)
        val context = coroutineContext
        log.info { "intercept method call with context: $context" }
    }
}
