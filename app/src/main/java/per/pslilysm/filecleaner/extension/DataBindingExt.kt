package per.pslilysm.filecleaner.extension

import per.pslilysm.filecleaner.entity.StorageStat
import java.util.concurrent.atomic.AtomicLong

/**
 * Extension for DataBinding
 *
 * @author caoxuedong
 * Created on 2023/12/02 21:59
 * @since 1.0
 */

fun AtomicLong?.getBoxed(): Long? {
    return this?.get()
}

fun Collection<*>?.sizeBoxed(): Int? {
    return this?.size
}

fun StorageStat?.usedSize(): Long? {
    return this?.usedSize
}

fun StorageStat?.totalSize(): Long? {
    return this?.totalSize
}