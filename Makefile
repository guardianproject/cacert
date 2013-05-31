all: debian-to-android

debian-verify:
	cd ca-certificates && git verify-tag `git describe --abbrev=0 --tags`

debian-certs: debian-verify
	$(MAKE) -C ca-certificates/ all

debian-to-android: debian-certs
	./pemsToAndroid.sh stores/debiancacerts.bks "ca-certificates/mozilla/*.crt"
