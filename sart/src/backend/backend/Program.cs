using backend.Context;
using backend.Helpers.interfaces;
using backend.Helpers;
using backend.repositroy;
using backend.repositroy.interfaces;
using backend.services;
using backend.services.interfaces;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using System.Text;
using System.Threading.RateLimiting;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddScoped<IUserService, UserService>();
builder.Services.AddScoped<IUserRepository, UserRepository>();
builder.Services.AddScoped<IGradRepository, GradRepository>();
builder.Services.AddScoped<IProstorijaRepository, ProstorijaRepository>();
builder.Services.AddScoped<INaseljeRepository, NaseljeRepository>();
builder.Services.AddScoped<IUlicaRepository, UlicaRepository>();
builder.Services.AddScoped<IRecordRepository, RecordRepository>();
builder.Services.AddScoped<IRecordService, RecordService>();
builder.Services.AddScoped<IRolaRepository, RolaRepository>();
builder.Services.AddScoped<ITableRepository, TableRepository>();
builder.Services.AddScoped<ITableService, TableService>();
builder.Services.AddScoped<IMapService, MapService>();
builder.Services.AddScoped<IObjectRepository, ObjectRepository>();
builder.Services.AddScoped<IObjectService, ObjectService>();
builder.Services.AddScoped<IDeviceRepository, DeviceRepository>();
builder.Services.AddScoped<IDeviceService, DeviceService>();
builder.Services.AddScoped<IRecordCalculation, RecordCalculation>();
builder.Services.AddScoped<IEmailService, EmailService>(); // za zaboravili ste lozinku
builder.Services.AddDbContext<AppDbContext>(options =>
{
    options.UseSqlite(builder.Configuration.GetConnectionString("DefaultConnection"));
});
builder.Services.AddCors(options => options.AddPolicy("TasksOrigins",
    policy =>
    {
        policy.AllowAnyMethod().AllowAnyHeader().SetIsOriginAllowed(origin => true).AllowCredentials();
    }));

// za tokene
builder.Services.AddAuthentication(x =>
{
    x.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    x.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
}).AddJwtBearer(x =>
{
    x.RequireHttpsMetadata = false;
    x.SaveToken = true;
    x.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuerSigningKey = true,
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes("veryverysecret.....")),
        ValidateAudience = false,
        ValidateIssuer = false
    };
});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors("TasksOrigins");

app.UseHttpsRedirection();

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();
