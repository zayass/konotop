package konotop

import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal actual fun <T : Any> KClass<T>.lookupFactory(vararg args: KSerializer<Any?>): ApiFactory<T>? {
    val qualifiedName = qualifiedName ?: return null

    val factoryClass = Class.forName("${qualifiedName}Factory")
    return factoryClass.kotlin.objectInstance as? ApiFactory<T>
}
