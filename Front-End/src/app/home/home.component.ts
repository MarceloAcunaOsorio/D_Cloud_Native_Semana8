import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MsalBroadcastService, MsalService } from '@azure/msal-angular';
import {
  AuthenticationResult,
  EventMessage,
  EventType,
  InteractionStatus,
} from '@azure/msal-browser';
import { filter } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';
import { DefaultBackendService } from '../service/default-backend.service';



type ProfileType = {
  name?: string;
  preferred_username?: string;
};



@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: [],
  standalone: true,
  imports: [CommonModule],
})
export class HomeComponent implements OnInit {
  loginDisplay = false;

  constructor(
    private readonly http: HttpClient, 
    private readonly backendService: DefaultBackendService,
    private readonly authService: MsalService,
    private readonly msalBroadcastService: MsalBroadcastService
  ) {}

  ngOnInit(): void {
    this.msalBroadcastService.msalSubject$.pipe(filter((msg: EventMessage) => msg.eventType === EventType.LOGIN_SUCCESS)).subscribe((result: EventMessage) => {console.log(result);const payload = result.payload as AuthenticationResult;this.authService.instance.setActiveAccount(payload.account);});
    this.msalBroadcastService.inProgress$.pipe(filter((status: InteractionStatus) => status === InteractionStatus.None)).subscribe(() => {this.setLoginDisplay();});
    this.getProfile(environment.apiConfig.uri)
  }

  setLoginDisplay() {
    this.loginDisplay = this.authService.instance.getAllAccounts().length > 0;
  }




   profile: ProfileType | undefined;
     responseBackend!: object;
   
   

   
     getProfile(url: string) {
       // Obtener el token del localStorage
       const token = localStorage.getItem('jwt');
     
       if (token) {
         try {
           // Decodificar el token sin usar jwt-decode (usando la función decodeTokenBase64Url)
           const decodedToken: any = this.decodeTokenBase64Url(token);
     
           // Extraer los datos deseados
           this.profile = {
             name: decodedToken.name,
             preferred_username: decodedToken.preferred_username,
           };
     
           console.log('Perfil decodificado:', this.profile);
         } catch (error) {
           console.error('Error al decodificar el token:', error);
         }
       } else {
         console.error('No se encontró ningún token en el localStorage.');
       }
     }
     
     private decodeTokenBase64Url(token: string): any {
       try {
         const base64Url = token.split('.')[1];
         const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
         const jsonPayload = decodeURIComponent(
           atob(base64)
             .split('')
             .map((c) => `%${c.charCodeAt(0).toString(16).padStart(2, '0')}`)
             .join('')
         );
         return JSON.parse(jsonPayload);
       } catch (error) {
         console.error('Error al decodificar el token:', error);
         return null;
       }
     }
     llamarBackend(): void {
       this.backendService.consumirBackend().subscribe(response => {
         this.responseBackend = response;
       });
     }
   
     mostrarResponseBackend(): string {
       return JSON.stringify(this.responseBackend);
     }



}
