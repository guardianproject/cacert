#!/bin/bash
#This script creates an Android compatibility certificate store from the current installed Debian certs in /etc/ssl/certs

certStore=$1
pemPath="$2"

rm $certStore
rm $certStore.txt

certAliasNum=0

for file in `dir -d $pemPath` ; do
	certAliasNum=$((certAliasNum+1))
	keytool -importcert -v -trustcacerts -file "$file" -alias $certAliasNum -keystore "$certStore" -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "libs/bcprov-jdk16-145.jar" -storetype BKS -storepass changeit -noprompt
	keytool -list -v -keystore "$certStore" -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "libs/bcprov-jdk16-145.jar" -storepass changeit -alias $certAliasNum >> $certStore.txt
done

