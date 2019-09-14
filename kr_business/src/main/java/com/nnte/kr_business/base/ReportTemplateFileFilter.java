package com.nnte.kr_business.base;

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
            if(name.endsWith(".xlt") || name.endsWith(".xltx"))
                return true;
            else
                return false;
        }
    }
}
