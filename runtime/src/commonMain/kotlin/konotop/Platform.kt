package konotop

import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

internal expect fun <T : Any> KClass<T>.lookupFactory(vararg args: KSerializer<Any?>): ApiFactory<T>?

