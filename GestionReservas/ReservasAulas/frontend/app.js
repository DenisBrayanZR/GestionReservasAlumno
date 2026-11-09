const api = window.API_URL || 'http://localhost:8080';
const $ = (id) => document.getElementById(id);
const mensaje = (texto, error = false) => {
  $('mensaje').textContent = texto;
  $('mensaje').className = error ? 'error' : 'ok';
};
async function cargar() {
  try {
    const [aulas, profesores, franjas, reservas] = await Promise.all(
      ['aulas', 'profesores', 'franjas-horarias', 'reservas'].map((ruta) =>
        fetch(`${api}/${ruta}`).then((r) => { if (!r.ok) throw new Error('No se pudo cargar la API'); return r.json(); })
      ));
    $('aula').innerHTML = aulas.map((a) => `<option value="${a.id}">${a.nombre} (${a.capacidad} plazas)</option>`).join('');
    $('profesor').innerHTML = profesores.map((p) => `<option value="${p.id}">${p.nombre}</option>`).join('');
    $('franjaHoraria').innerHTML = franjas.map((f) => `<option value="${f.id}">${f.horaInicio} - ${f.horaFin}</option>`).join('');
    $('reservas').innerHTML = reservas.length ? reservas.map((r) =>
      `<article class="card"><strong>${r.fechaReserva}</strong> · ${r.aula.nombre}<br>${r.franjaHoraria.horaInicio} - ${r.franjaHoraria.horaFin} · ${r.profesor.nombre}</article>`
    ).join('') : '<p>No hay reservas todavía.</p>';
  } catch (error) { mensaje(error.message, true); }
}
$('reserva-form').addEventListener('submit', async (event) => {
  event.preventDefault();
  const body = {
    fechaReserva: $('fechaReserva').value,
    aula: { id: Number($('aula').value) },
    profesor: { id: Number($('profesor').value) },
    franjaHoraria: { id: Number($('franjaHoraria').value) }
  };
  const response = await fetch(`${api}/reservas`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) });
  const data = await response.json().catch(() => ({}));
  if (!response.ok) return mensaje(data.error || 'No se pudo crear la reserva', true);
  mensaje('Reserva creada correctamente');
  event.target.reset();
  cargar();
});
$('recargar').addEventListener('click', cargar);
cargar();
