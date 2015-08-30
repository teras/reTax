include dist/config
include dist/config.mak

DIR=$(shell ls .. | grep ${NAME})

all:
	mkdir -p tmpclasses
	mkdir -p classes
	mkdir -p bin
	source dist/config ; source dist/methods.sh ; find_classes; make_manifest bin/MANIFEST.MF
	cd bin ; ${JAVAC}  -bootclasspath ${CLDCAPI}${PATHSEP}${MIDPAPI} -d ../tmpclasses -classpath ../tmpclasses `find ../src -name '*'.java`
	cd bin ; ${PREVERIFY} -classpath ${CLDCAPI}${PATHSEP}${MIDPAPI}${PATHSEP}../tmpclasses -d ../classes ../tmpclasses
	cd bin ; ${JAR} cmf MANIFEST.MF ${NAME}.jar -C ../classes .
	cd bin ; if [ -d ../res ] ; then ${JAR} uf ${NAME}.jar -C ../res . ; fi
	JARSIZE=`ls -l bin/${NAME}.jar | ${AWK} '{print $$5}'` ; cp bin/MANIFEST.MF bin/${NAME}.jad ; echo >> bin/${NAME}.jad "MIDlet-Jar-Size: $$JARSIZE"
	echo >> bin/${NAME}.jad "MIDlet-Jar-URL: ${NAME}.jar"

dist/config.mak:
	./configure

clean:
	rm -rf tmpclasses
	rm -rf classes
	rm -rf tmplib
	rm -f bin/MANIFEST.MF

confclean:clean
	rm -f dist/config.mak

distclean:confclean
	rm -f bin/${NAME}.jar
	rm -f bin/${NAME}.jad
	if [ -d bin ] ; then rmdir -p bin ; fi
	rm -f ${DIR}.tar.bz2

run:
	${WTK_HOME}/bin/emulator -Xdescriptor:`pwd`/bin/${NAME}.jad

dist-bz2:
	./configure
	make distclean
	make all
	make confclean
	cd .. ; tar -jcvhf ${DIR}.tar.bz2 ${DIR}
	mv ../${DIR}.tar.bz2 .
