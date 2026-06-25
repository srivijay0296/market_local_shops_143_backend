@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@IF "%MAVEN_BATCH_ECHO%" == "on"  echo %MAVEN_BATCH_ECHO%

@SETLOCAL

@SET "DIRNAME=%~dp0"
@IF "%DIRNAME%" == "" SET "DIRNAME=.\"

@IF "%MAVEN_SKIP_RC%" == "" (
  @IF EXIST "%USERPROFILE%\mavenrc_pre.bat" CALL "%USERPROFILE%\mavenrc_pre.bat"
  @IF EXIST "%DIRNAME%mavenrc_pre.bat" CALL "%DIRNAME%mavenrc_pre.bat"
)

@SET "WRAPPER_JAR=%DIRNAME%\.mvn\wrapper\maven-wrapper.jar"
@SET "WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain"

@SET "DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
@IF NOT EXIST "%WRAPPER_JAR%" (
    echo -n Downloading %DOWNLOAD_URL%
    powershell -Command "&{"^
		"$webclient = new-object System.Net.WebClient;"^
		"if (-not ([string]::IsNullOrEmpty('%MVNW_USERNAME%') -and [string]::IsNullOrEmpty('%MVNW_PASSWORD%'))) {"^
		"$webclient.Credentials = new-object System.Net.NetworkCredential('%MVNW_USERNAME%', '%MVNW_PASSWORD%');"^
		"}"^
		"[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $webclient.DownloadFile('%DOWNLOAD_URL%', '%WRAPPER_JAR%')"^
		"}"
)

@IF EXIST "%JAVA_HOME%\bin\java.exe" (
  @SET "JAVACMD=%JAVA_HOME%\bin\java.exe"
) ELSE (
  @SET "JAVACMD=java"
)

"%JAVACMD%" ^
  %MAVEN_OPTS% ^
  -classpath "%WRAPPER_JAR%" ^
  "-Dmaven.multiModuleProjectDirectory=%DIRNAME%" ^
  %WRAPPER_LAUNCHER% %*

@IF ERRORLEVEL 1 goto error
@goto end

:error
@SET ERROR_CODE=1

:end
@IF "%MAVEN_BATCH_PAUSE%" == "on" pause
@IF "%MAVEN_SKIP_RC%" == "" (
  @IF EXIST "%DIRNAME%mavenrc_post.bat" CALL "%DIRNAME%mavenrc_post.bat"
  @IF EXIST "%USERPROFILE%\mavenrc_post.bat" CALL "%USERPROFILE%\mavenrc_post.bat"
)
@EXIT /B %ERROR_CODE%
