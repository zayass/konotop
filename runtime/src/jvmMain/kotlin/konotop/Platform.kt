package konotop

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal actual fun <T : Any> KClass<T>.lookupFactory(): ApiFactory<T>? {
    val annotation = java.getAnnotation(AssociatedFactory::class.java) ?: return null
    return annotation.factory.objectInstance as? ApiFactory<T>
}
