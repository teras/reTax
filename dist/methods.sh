#!/bin/sh

source dist/config.mak

find_classes () {
	FILES=""
	FILENAMES=`find src -name '*'.java | xargs grep --with-filename MIDlet | grep extends | tr [:] [\ ] | $AWK '{print $1}'`
	for i in ${FILENAMES} ; do
		FN=`echo ${i} | sed -e "s/src\///g" | sed -e "s/\.java$//g" | tr [/] [.]`
		if [ "${FILES}" == "" ] ; then
			FILES=${FN}
		else
			FILES="${FILES}, ${FN}"
		fi
	done
}

# $1 = manifest position
# $2 = Name
# $3 = Desctiption
# $4 = Icon Position
# $5 = Filesa
make_manifest () {
	LCNAME=`echo $NAME|tr [A-Z] [a-z]`
	cat >bin/MANIFEST.MF <<EOF
MIDlet-1: $NAME, ${ICONS}$LCNAME.png, ${FILES}
MIDlet-Description: ${DESC}
MIDlet-Name: ${NAME}
MIDlet-Vendor: Panayotis Katsaloulis
MIDlet-Version: 1.0
MicroEdition-Configuration: CLDC-1.1
MicroEdition-Profile: MIDP-2.0
EOF
}
