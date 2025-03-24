package tools

import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

object WriteJpegUserComment {
    /**
     * 修改jpeg文件exif数据体中UserComment的值
     * 参数：
     *  - 原始jpeg文件路径
     *  - UserComment内容文件路径（读取文件内容转为Base64再写入）
     *  - 输出的jpeg文件路径
     */
    fun main(args: Array<String>) {
        require(args.size >= 3) { "error args, please use: java -jar this_file.jar inputJpegPath userCommentContentFilePath outputJpegPath" }

        val inputJpegPath = args[0]
        val userCommentContentFilePath = args[1]
        val outputJpegPath = args[2]

        val imageFile = File(inputJpegPath)
        val value = Base64.getEncoder().encodeToString(File(userCommentContentFilePath).readBytes())
        val outputFile = File(outputJpegPath)

        FileInputStream(imageFile).use { fis ->
            FileOutputStream(outputFile).use { fos ->
                // 读取 EXIF 数据
                val outputSet = runCatching {
                    if (Imaging.getMetadata(imageFile) != null) (Imaging.getMetadata(imageFile) as TiffImageMetadata).outputSet
                    else TiffOutputSet()
                }.getOrDefault(TiffOutputSet())

                // 修改 Image Description (Tag ID: 0x010E)
                val exifDir = outputSet.getOrCreateExifDirectory()
                exifDir.removeField(ExifTagConstants.EXIF_TAG_USER_COMMENT)
                exifDir.add(ExifTagConstants.EXIF_TAG_USER_COMMENT, value)

                // 写回新的 EXIF 数据
                ExifRewriter().updateExifMetadataLossless(fis, fos, outputSet)
            }
        }
    }

}