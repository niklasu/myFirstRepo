package niklasu.speedtester.config

import com.google.inject.Inject
import niklasu.speedtester.MiB
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

class ParamValidator @Inject constructor(private val fileSizeChecker: FileSizeChecker) {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    @Throws(ValidationException::class)
    fun validate(config: Config) {
        logger.debug("Validating {}", config)
        if (config.fileSize < 1) throw ValidationException("download size must be >=1")
        val realFileSize: Long
        try {
            val url = URL(config.url)
            realFileSize = fileSizeChecker.getFileSize(url)
        } catch (e: MalformedURLException) {
            throw ValidationException("Malformed URL. It has to start with http://", e)
        } catch (e: NumberFormatException) {
            throw ValidationException("Unable to get the file size for ${config.url}", e)
        } catch (e: IOException) {
            throw ValidationException("Unable to get the file size for ${config.url}", e)
        }

        if (realFileSize < MiB) throw ValidationException("The size of ${config.url} was $realFileSize and is < $MiB")
        if (realFileSize < config.fileSize) throw ValidationException("${config.url} has a size of $realFileSize while ${config.fileSize * MiB} is required")

        if (config.interval < 1) throw ValidationException("Interval must be >= 1. Your input was ${config.interval}")

    }
}
