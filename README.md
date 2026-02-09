# NOTICE
This repository is an English translation of [唐家林 (English: Tang Jialin)](https://github.com/sudot)'s [jna-golang-sample](https://github.com/sudot/jna-golang-sample). 

**If you find this repository useful, please give the original repository a star**

Unfortunately, I cannot read Mandarin, nor speak Chinese. 
Thus, these translations are machine-generated, 
and I make absolutely zero guarantees regarding their correctness. 

# jna-golang-sample

Use Java to call Go via JNA (Java Native Access), and solve the return-value memory cleanup issue in two ways: InvocationMapper and TypeMapper.

This project uses CGO, so cross-compilation is not supported. Although you can use https://github.com/karalabe/xgo
 as a workaround, it’s more convenient to compile directly on the target VM.

## Environment Setup
### 1. Install the Go SDK

Official download: https://golang.org/dl/

Faster mirror: https://studygolang.com/dl

Install it anywhere you like, and you’re done.

### 2. Set environment variables

- GOROOT

  > `golang sdk <installation directory>`

- GOPATH

The location where code is stored. However, starting from Go 1.11, source code no longer needs to be placed here, so this directory is used to store third-party source code that Go depends on.
  > `go <go source location>`

```bash
# This path is for demonstration only; use the actual path where your SDK is installed.
GOROOT=D:\dev\go
# This path is for demonstration only; use any path you prefer.
GOPATH=D:\gopath
```



### 3. Go module proxy

Open a terminal and run the following commands：

```bash
go env -w GOPROXY=https://goproxy.cn,direct
```



## 4. Install GCC

### Windows installation method

[MinGW Download](http://sourceforge.net/projects/mingw-w64/files/Toolchains%20targetting%20Win32/Personal%20Builds/mingw-builds/installer/mingw-w64-install.exe/download)

> Note: Pay attention to selecting the correct target platform during installation.
>
> ![1610680778788](README/1610680778788.png)

## Build commands

### General build

```bash
# build windows version
go build -buildmode=c-shared -o awesome.dll awesome.go

# build linux verison
go build -buildmode=c-shared -o awesome.so awesome.go

# build mac version
go build -buildmode=c-shared -o libawesome.dylib awesome.go
```

Cross-compilation

This project uses CGO, so cross-compilation is not supported. Although it can be done via https://github.com/karalabe/xgo
, it is more convenient to install a target VM and compile directly on it.

Cross-compilation in Go means building a binary for a platform different from the one you are currently on.

For example: developing on Windows but needing to build a Linux or macOS version.

You can view the current system variables with go env:

```bash
> go env
set GOHOSTARCH=amd64 # Host machine architecture
set GOHOSTOS=windows # Host operating system
set GOARCH=amd64     # Target platform architecture (must be set for cross-compilation)
set GOOS=windows     # Target platform operating system (must be set for cross-compilation)
set CGO_ENABLED=0    # Whether CGO is enabled
```

The key to cross-compilation is specifying two variables at build time:

- GOOS

  > Target operating system

- GOARCH

  > Target platform architecture

The valid values for these two variables are listed later and can be looked up as needed. You can also check them using the Go SDK command go tool dist list.

Below are the commonly used build commands for Windows, Linux, and macOS.


#### Building on Windows

```bash
# build windows version
SET CGO_ENABLED=0
SET GOOS=windows
SET GOARCH=amd64
go build -buildmode=c-shared -o awesome.dll awesome.go

# build linux verison
SET CGO_ENABLED=0
SET GOOS=linux
SET GOARCH=amd64
go build -buildmode=c-shared -o awesome.so awesome.go

# build mac version
SET CGO_ENABLED=0
SET GOOS=darwin
SET GOARCH=amd64
go build -buildmode=c-shared -o awesome.so awesome.go
```

#### Building on Linux/Mac

```bash
# build windows version
CGO_ENABLED=0 GOOS=windows GOARCH=amd64 go build -buildmode=c-shared -o awesome.dll awesome.go
# build linux verison
CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -buildmode=c-shared -o awesome.so awesome.go
# build mac version
CGO_ENABLED=0 GOOS=darwin GOARCH=amd64 go build -buildmode=c-shared -o awesome.so awesome.go
```

### GOOS and GOARCH supported OS list

View the compilation platforms supported by the current Go version

```bash
go tool dist list
```
