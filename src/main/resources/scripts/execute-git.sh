#!/usr/bin/env sh

cd `dirname $1`

#| Parameter | Description                  |
#|-----------|------------------------------|
#| $1        | The git project directory.   |
#| $2        | The git executable.          |
#| $3*       | The git command arguments.   |

orig_dir=`pwd`
cd $1

shift
git_cmd=$1

shift

while [ "$1" != "" ] 
do
	git_cmd="$git_cmd $1"
	shift
done

$git_cmd

cd $orig_dir


