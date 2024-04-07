package konotop.kotlin.compiler.plugin

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.packageFqName
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object Qualifiers {
    val packageName = FqName("konotop")

    val ApiFactory = packageName.childClass("ApiFactory")
    val AssociatedFactory = packageName.childClass("AssociatedFactory")
}

fun IrClass.factoryClassId(): ClassId? {
    val packageName = packageFqName ?: return null
    val className = name.asString()

    return ClassId(
        packageName,
        Name.identifier("${className}Factory")
    )
}

private fun FqName.childClass(name: String) =
    ClassId(this, Name.identifier(name))