#!/bin/bash

# configurations

var_version=0.1.1


# discover BASEDIR
#BASEDIR=`dirname "$0"`/..  # located on ROOT_DIR/bin
BASEDIR=`dirname "$0"`/.   # located on ROOT_DIR
BASEDIR=`(cd "$BASEDIR"; pwd)`
ls -l "$0" | grep -e '->' > /dev/null 2>&1
if [ $? = 0 ]; then
  #this is softlink
  _PWD=`pwd`
  _EXEDIR=`dirname "$0"`
  cd "$_EXEDIR"
  _BASENAME=`basename "$0"`
  _REALFILE=`ls -l "$_BASENAME" | sed 's/.*->\ //g'`
   BASEDIR=`dirname "$_REALFILE"`/..
   BASEDIR=`(cd "$BASEDIR"; pwd)`
   cd "$_PWD"
fi


# os type detection function
ostype () {
  local OSNAME="`uname`"
  case $OSNAME in
    'Linux')
      OSNAME='Linux'

      cat /etc/*-release | grep -i 'CentOS' > /dev/null
      if [ $? -eq 0 ];
      then
          OSNAME='CentOS'
      else
        cat /etc/*-release | grep -i 'Ubuntu' > /dev/null
        if [ $? -eq 0 ];
        then
          OSNAME='Ubuntu'
        fi
      fi

      ;;

    'FreeBSD')
      OSNAME='FreeBSD'
      alias ls='ls -G'
      ;;
    'WindowsNT')
      OSNAME='Windows'
      ;;
    'Darwin')
      OSNAME='Mac'
      ;;
    'SunOSNAME')
      OSNAME='Solaris'
      ;;
    'AIX') ;;
    *) ;;
  esac
  echo  "$OSNAME"
}


#
# working area
#

var_chdir=target/autorest4db-$var_version
var_prefix=/usr/local/autorest4db
var_after_install=$BASEDIR/src/main/assembly/share/after_install.sh

var_target='deb'
var_osname="$(ostype)"
case $var_osname in
  'Mac')
    var_target='osxpkg'
  ;;
  'Ubuntu') # Ubuntu apt-get deb
    var_target='deb'
  ;;
  'CentOS') # CentOS yum rpm
    var_target='rpm'
  ;;
  '*')
    var_target='deb'
  ;;
esac

# gen after_install.sh

# package description
pk_url="https://github.com/downgoon/autorest4db"
pk_vendor="downgoon"
pk_maintainer="downgoon@qq.com"
pk_category="web"
pk_description="automatically generate a RESTful API of your database (sqlite3 or mysql) in runtime."

echo "packaging ..."
echo fpm -f  --verbose -s dir -t $var_target --deb-no-default-config-files  --deb-auto-config-files  -n autorest4db -v $var_version -C $var_chdir  --package $BASEDIR  --prefix $var_prefix --after-install $var_after_install --url "$pk_url" --vendor "$pk_vendor" --category "$pk_category" --description "$pk_description" --maintainer="$pk_maintainer"

# package (deb format)
exec fpm -f  --verbose -s dir -t $var_target --deb-no-default-config-files  --deb-auto-config-files  -n autorest4db -v $var_version -C $var_chdir  --package $BASEDIR  --prefix $var_prefix --after-install $var_after_install --url "$pk_url" --vendor "$pk_vendor" --category "$pk_category" --description "$pk_description" --maintainer="$pk_maintainer"
