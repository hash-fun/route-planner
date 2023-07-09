package ru.sfedu.geo.util

import org.slf4j.LoggerFactory.getLogger

@Suppress("UnusedReceiverParameter")
inline fun <reified T : Any> T.logger() = getLogger(T::class.java)!!

inline fun <reified T : Any> T.lazyLogger() = lazy { logger() }

