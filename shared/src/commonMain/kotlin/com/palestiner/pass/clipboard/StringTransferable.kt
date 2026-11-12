package com.palestiner.pass.clipboard

import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException

class StringTransferable(private val str: String) : Transferable {

    private val supported: Array<DataFlavor> = arrayOf(DataFlavor.stringFlavor)

    override fun getTransferDataFlavors(): Array<DataFlavor> {
        return supported
    }

    override fun isDataFlavorSupported(flavor: DataFlavor?): Boolean {
        return DataFlavor.stringFlavor.equals(flavor)
    }

    override fun getTransferData(flavor: DataFlavor?): Any {
        if (!isDataFlavorSupported(flavor)) {
            throw UnsupportedFlavorException(flavor)
        }
        return str
    }
}
