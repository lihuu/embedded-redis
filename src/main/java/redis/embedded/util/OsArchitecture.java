package redis.embedded.util;

import com.google.common.base.Preconditions;

public record OsArchitecture(OS os, Architecture arch) {

    public static final OsArchitecture WINDOWS_x86 = new OsArchitecture(OS.WINDOWS, Architecture.x86);
    public static final OsArchitecture WINDOWS_x86_64 = new OsArchitecture(OS.WINDOWS, Architecture.x86_64);

    public static final OsArchitecture UNIX_x86 = new OsArchitecture(OS.UNIX, Architecture.x86);
    public static final OsArchitecture UNIX_x86_64 = new OsArchitecture(OS.UNIX, Architecture.x86_64);

    public static final OsArchitecture MAC_OS_X_x86 = new OsArchitecture(OS.MAC_OS_X, Architecture.x86);
    public static final OsArchitecture MAC_OS_X_x86_64 = new OsArchitecture(OS.MAC_OS_X, Architecture.x86_64);

    public static final OsArchitecture MAC_OS_X_ARM_64 = new OsArchitecture(OS.MAC_OS_X,Architecture.ARM64);

    public static OsArchitecture detect() {
        OS os = OSDetector.getOS();
        Architecture arch = OSDetector.getArchitecture();
        return new OsArchitecture(os, arch);
    }

    public OsArchitecture {
        Preconditions.checkNotNull(os);
        Preconditions.checkNotNull(arch);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OsArchitecture that = (OsArchitecture) o;

        return arch == that.arch && os == that.os;

    }

}
