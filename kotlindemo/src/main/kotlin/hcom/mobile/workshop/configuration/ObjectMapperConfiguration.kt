package hcom.mobile.workshop.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans

val objectMapperBeanDefinition = beans {
    bean {
        ObjectMapper()
            .registerModule(KotlinModule()) // to deserialize Kotlin data classes
            .registerModule(JavaTimeModule()) // because of Instant
    }
}

class ObjectMapperBeanInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(context: GenericApplicationContext) = objectMapperBeanDefinition.initialize(context)
}
