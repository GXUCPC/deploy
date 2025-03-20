#!/bin/bash

chmod +x branchpull.sh

source config.sh

if [ -d "Gxucpc-system" ]; then
    rm -rf Gxucpc-system || { echo "没法删除文件夹"; exit 1; }
    echo "原仓库已经删除"
fi

git clone $REPO_URL || { echo "git clone 失败"; exit 1;}
echo "仓库 '$REPO_URL' 已克隆"


cd Gxucpc-system || { echo "无法进入目录"; exit 1;}

read -p "请输入想要切换的分支名称（默认为 $Branch ):" user_branch

if [ -z "$user_branch" ]; then
    user_branch="$Branch"
fi

git checkout "$user_branch" || { echo "git checkout 失败"; exit 1;}
git status

mvn package -DskipTests=true || { echo "mvn package 失败"; exit 1;}
nohup java -jar  Gxucpc-system\target\Gxucpc-system-0.0.1-SNAPSHOT.jar  >> mylog.log 2>&1 &
