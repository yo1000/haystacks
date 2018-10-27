package com.yo1000.haystacks.web.controller

import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.nio.file.Path

/**
 *
 * @author yo1000
 */
@RequestMapping("/note")
open class NoteController(
        private val noteFilePath: Path
) {
    @GetMapping(value = ["/download/*"], produces = ["${MediaType.APPLICATION_OCTET_STREAM_VALUE}; charset=UTF-8; Content-Disposition: attachment"])
    @ResponseBody
    fun getDownload(): Any = noteFilePath.toFile().let { InputStreamResource(it.inputStream()) }
}
