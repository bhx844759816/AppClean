// IPackageDeleteObserver.aidl
package android.content.pm;

interface IPackageDeleteObserver {
   oneway void packageDeleted(in String packageName, in int returnCode);
}