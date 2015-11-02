#!/bin/bash
#This script creates an Android compatibility certificate store from the current installed Debian certs in /etc/ssl/certs
#

if [ -z "$1" ]; then
    echo "./pemsToAndroid.sh certstore.bks \"/path/to/pems/*.pem\""
    echo "EXAMPLE: ./pemsToAndroid.sh debiancacertstore.bks \"/etc/ssl/certs/*.pem\""
    exit 1
fi

keyStore=$1
pemPath="$2"

for f in $pemPath
do
	alias=$(basename $f)
	echo adding alias $alias
	keytool -importcert -v -trustcacerts -file $f -alias $alias -keystore $keyStore -providername BC -providerclass org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "libs/bcprov-jdk15on-153.jar" -storetype BKS -storepass changeit -noprompt
done

