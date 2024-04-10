package konotop

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class AssociatedFactory(val factory: KClass<out ApiFactory<*>>)
