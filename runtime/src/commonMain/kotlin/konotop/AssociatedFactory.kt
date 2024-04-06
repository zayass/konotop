package konotop

import kotlin.reflect.KClass

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect annotation class AssociatedFactory(val factory: KClass<out ApiFactory<*>>)