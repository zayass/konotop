package konotop.kotlin.compiler.plugin

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.impl.IrClassReferenceImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.starProjectedType
import org.jetbrains.kotlin.ir.util.*

class MarkerAnnotationIrGenerator(
    private val irClass: IrClass,
    private val factoryIrClass: IrClass,
    private val compilerContext: PluginContext,
) {

    companion object {
        private fun PluginContext.findFactory(irClass: IrClass): IrClass? {
            if (irClass.kind != ClassKind.INTERFACE) {
                return null
            }

            val factoryClassId = irClass.factoryClassId() ?: return null
            val factoryClass = referenceClass(factoryClassId) ?: return null

            if (factoryClass.owner.kind != ClassKind.OBJECT) {
                return null
            }

            val apiFactoryClass = apiFactoryClass ?: return null

            return factoryClass.owner.takeIf {
                it.isSubclassOf(apiFactoryClass.owner)
            }
        }

        fun generate(irClass: IrClass, context: PluginContext) {
            val factoryIrClass = context.findFactory(irClass)
            if (factoryIrClass != null) {
                MarkerAnnotationIrGenerator(irClass, factoryIrClass, context).generate()
            }
        }
    }

    private fun generate() {
        compilerContext.logger.logging("Service class found: ${irClass.name.asString()}")

        irClass.patchServiceInterfaceWithMarkerAnnotation(factoryIrClass)
    }

    private fun IrClass.patchServiceInterfaceWithMarkerAnnotation(factoryClass: IrClass) {
        val annotationMarkerClass = compilerContext.annotationMarkerClass ?: return

        val associatedFactoryAlreadyPresent = annotations.any {
            it.constructedClass.fqNameWhenAvailable == annotationMarkerClass.owner.fqNameWhenAvailable
        }

        if (associatedFactoryAlreadyPresent) {
            return
        }

        annotations += createFactoryAnnotation(annotationMarkerClass, factoryClass)
    }

    private fun IrClass.createFactoryAnnotation(
        annotationClass: IrClassSymbol,
        factoryClass: IrClass
    ): IrConstructorCallImpl {
        val annotationCtor = annotationClass.constructors.single { it.owner.isPrimary }
        val annotationType = annotationClass.defaultType

        return IrConstructorCallImpl.fromSymbolOwner(
            startOffset = startOffset,
            endOffset = endOffset,
            type = annotationType,
            constructorSymbol = annotationCtor
        ).apply {
            putValueArgument(
                0,
                createClassReference(
                    factoryClass.defaultType,
                    startOffset,
                    endOffset
                )
            )
        }
    }

    private val IrConstructorCall.constructedClass
        get() = symbol.owner.constructedClass

    private fun createClassReference(classType: IrType, startOffset: Int, endOffset: Int): IrClassReference {
        return IrClassReferenceImpl(
            startOffset = startOffset,
            endOffset = endOffset,
            type = compilerContext.irBuiltIns.kClassClass.starProjectedType,
            symbol = classType.classifierOrFail,
            classType = classType
        )
    }
}
