package konotop

import kotlin.reflect.AssociatedObjectKey
import kotlin.reflect.ExperimentalAssociatedObjects
import kotlin.reflect.KClass

@OptIn(ExperimentalAssociatedObjects::class)
@AssociatedObjectKey
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
internal annotation class AssociatedFactory(val factory: KClass<out ApiFactory<*>>)
