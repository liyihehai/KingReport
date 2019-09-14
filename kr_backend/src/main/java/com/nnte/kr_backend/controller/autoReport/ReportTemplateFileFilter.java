package com.nnte.kr_backend.controller.autoReport;

import java.io.File;
import java.io.FileFilter;

public class ReportTemplateFileFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        if(pathname.isDirectory())
            return false;
        else
        {
            String name = pathname.getName();
            if(name.endsWith(".xlxt") || name.endsWith(".xltx"))
                return true;
            else
                return false;
        }
    }
}
