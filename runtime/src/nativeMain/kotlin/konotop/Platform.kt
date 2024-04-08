package konotop

import kotlin.reflect.ExperimentalAssociatedObjects
import kotlin.reflect.KClass
import kotlin.reflect.findAssociatedObject

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalAssociatedObjects::class)
internal actual fun <T : Any> KClass<T>.lookupFactory(): ApiFactory<T>? =
    when (val assocObject = findAssociatedObject<AssociatedFactory>()) {
        is ApiFactory<*> -> assocObject as ApiFactory<T>
        else -> null
    }
