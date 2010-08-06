package net.sourcewalker.picrename;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

public class ExifReader {

    private static final String EXIF_DATE_FORMAT = "yyyy:MM:dd HH:mm:ss";

    private final File source;
    private boolean successful;
    private Date dateTaken;

    public ExifReader(File source) {
        this.source = source;

        readInfo();
    }

    private void readInfo() {
        try {
            IImageMetadata metadata = Sanselan.getMetadata(source);
            if (metadata instanceof JpegImageMetadata) {
                JpegImageMetadata jpegMeta = (JpegImageMetadata) metadata;
                TiffField exifDate = jpegMeta
                        .findEXIFValue(TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
                if (exifDate != null) {
                    String date = (String) exifDate.getValue();
                    dateTaken = parseExifDate(date);
                    successful = true;
                }
            }
        } catch (ImageReadException e) {
        } catch (IOException e) {
            throw new RuntimeException("IO Error while getting metadata: "
                    + e.getMessage(), e);
        }
    }

    private Date parseExifDate(String date) {
        DateFormat format = new SimpleDateFormat(EXIF_DATE_FORMAT, Locale.ROOT);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Can't parse date: " + date);
        }
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public boolean isSuccessful() {
        return successful;
    }

}
