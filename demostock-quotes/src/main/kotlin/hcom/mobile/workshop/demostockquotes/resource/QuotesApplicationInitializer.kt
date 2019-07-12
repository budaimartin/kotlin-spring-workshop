package hcom.mobile.workshop.demostockquotes.resource

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext

class QuotesApplicationInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(context: GenericApplicationContext) = beanz().initialize(context)
}
