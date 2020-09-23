[string]$jarFile =  "$PSScriptRoot\JTeeProxy.jar";
if (![System.IO.File]::Exists($jarFile)) {
    Write-Error -Message "The file $jarFile does not exist. Please build project first." 
} else {
    start powershell { 
        $host.ui.RawUI.WindowTitle = “Server A”
        Write-Host "Server A started. Exit with [Strg]+[C]"
        java -cp .\JTeeProxy.jar net.jteeproxy.testtools.EchoServer 6789 
    }

    start powershell { 
        $host.ui.RawUI.WindowTitle = “Server B”
        Write-Host "Server B started. Exit with [Strg]+[C]"
        java -cp .\JTeeProxy.jar net.jteeproxy.testtools.EchoServer 6790 
    }

    start powershell { 
        $host.ui.RawUI.WindowTitle = “JTeeProxy”
        Write-Host "JTeeProxy started. Exit with [Strg]+[C]"
        java -cp .\JTeeProxy.jar net.jteeproxy.JTeeProxy 1234 localhost 6789 localhost 6790 
    }

    start powershell { 
        $host.ui.RawUI.WindowTitle = “Echo Client”
        Write-Host "Client connected to JTeeProxy. Exit with [Strg]+[C]. Type some text and press [Enter]:"
        java -cp .\JTeeProxy.jar net.jteeproxy.testtools.EchoClient localhost 1234
    }
}