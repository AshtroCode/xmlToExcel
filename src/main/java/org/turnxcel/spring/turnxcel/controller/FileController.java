package org.turnxcel.spring.turnxcel.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.turnxcel.turnxcel.converter.ConfigXmlToExcel;
import org.turnxcel.turnxcel.xmlreader.XmlParserT4;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private XmlParserT4 xmlParserLTE;

    @Autowired
    private ConfigXmlToExcel configXmlToExcel;

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            Map<String, String> parameterData = xmlParserLTE.readXML(file);
            ByteArrayOutputStream outputStream = configXmlToExcel.writeExcel(parameterData);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=converted.xlsx")
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            logger.error("Failed to process file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process file: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcelFile(@RequestParam("file") byte[] excelBytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "converted.xlsx");
        headers.setContentLength(excelBytes.length);

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
    @GetMapping("/health") // Add this endpoint
    public ResponseEntity<String> healthCheck() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        System.out.println("*********************************");
        System.out.println("Health OK | Time: "+ strDate);
        return ResponseEntity.ok("OK");
    }
}
