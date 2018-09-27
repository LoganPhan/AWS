#!/usr/bin/env bash

remove_snapshot()
{
    echo "Remove SNAPSHOT started"
    mvn  versions:set -DremoveSnapshot
    VERSIONSTATUS=$?
    if [ $VERSIONSTATUS -eq 0 ]; then
        echo "Remove SNAPSHOT Successful"
        build_project
    else
        echo "Remove SNAPSHOT Failed"
    fi
}

build_project()
{
    echo "Build started"
    mvn  clean deploy
    BUILDSTATUS=$?
    if [ $BUILDSTATUS -eq 0 ]; then
        echo "Build Successful"
        revert_snapshot
    else
        echo "Build Failed"
        revert_snapshot
    fi
}


revert_snapshot()
{
    echo "Revert Started"
    mvn versions:revert
    REVERTSTATUS=$?
    if [ $REVERTSTATUS -eq 0 ]; then
        echo "Revert Successful"
    else
        echo "Revert Failed"
    fi
}

###
# Main body of script starts here
###
echo "Start of script..."
remove_snapshot
echo "End of script..."